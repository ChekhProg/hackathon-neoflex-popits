package rht.hack

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}

import rht.common.domain.candles.{Candle, CandleDetails, common}

import scala.concurrent.duration.FiniteDuration
import com.redis.{M, PubSubMessage, RedisClient, S, U}
import org.json4s._
import org.json4s.jackson.JsonMethods._


/**
  * Program entry point
  *
  * You can create another one temporarily to test your code,
  * but the final entry point must be this one!
  */
object Main extends HackathonApp {

  /**
    * Your "main" function
    *
    * @param args program arguments
    * @return Actor, which will be used to push events to Akka Stream
    */
  override def start(args: List[String]): SourceActor = {
    // TODO: Implement this method to start your app

    def percentCalculate(r: RedisClient , figi: String, close: BigDecimal): BigDecimal = {
      r.get(figi) match {
        case Some(x) =>
          val closeOld: BigDecimal = readClose(x)
          val percent = (close - closeOld) / closeOld
          percent
        case None => BigDecimal(0)
      }
    }

    def readClose(s: String): BigDecimal = {
      val json = parse(s)
      val close = {
        json \ "close"
      }
      BigDecimal(close.values.toString)
    }

    def write(det: CandleDetails, per: BigDecimal) = {
      val obj = JObject(List(
        "low" -> JDecimal(det.low),
        "high" -> JDecimal(det.high),
        "open" -> JDecimal(det.open),
        "close" -> JDecimal(det.close),
        "percent" -> JDecimal(per)
      ))
      val doc = render(obj)
      val json = pretty(doc)
      json
    }

    val r = new RedisClient("localhost", 6379)
    val p = new RedisClient("redis-18061.c275.us-east-1-4.ec2.cloud.redislabs.com", 18061, secret=Some("ZUBAw2FrqMaEEnixY8crKIPzoNWp4CfI"))

    class MyActor(r: RedisClient) extends Actor {
      val log: LoggingAdapter = Logging(context.system, this)

      def receive: Receive = {
        case x @ Candle(interval: FiniteDuration, figi: common.Figi, details: CandleDetails) =>
          val percent = percentCalculate(r, figi.value, details.close)
          log.info(figi.value + " " + details.close + " " + percent)
          val json = write(x.details, percent)
          r.set(figi.value, json)
          //log.info("candle: " + figi.value + "\n" + json)
        case x      => log.info("received unknown message: " + x.getClass)
      }
    }

    val system: ActorSystem = ActorSystem("ActorGuide")

    val pinger = system.actorOf(Props(classOf[MyActor], p), "pinger")

    pinger
  }

}
