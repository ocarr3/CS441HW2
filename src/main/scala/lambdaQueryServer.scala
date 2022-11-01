import HelperUtils.{CreateLogger, ObtainConfigReference}
import com.hellogrpc.hello.{lambdaInvoke, lambdaQueryGrpc, lambdaResponse}
import com.typesafe.config.Config
import io.grpc.{Server, ServerBuilder}
import org.slf4j.Logger
import requests.Response
import scala.concurrent.{ExecutionContext, Future}
// Server using ScalaPB and gRPC protobufs to communicate and invoke a lambda function (does not use Akka despite the name for the directory, please ignore)
// Listens on port from the configuration file and uses protobufs from 'hello.proto' to communicate with the client in 'main' file
// Creates a request for the lambda function's API Gateway from the configuration file sending the received messages from the client as a payload
// Response from the lambda function is grabbed from the response and sent back the client

object lambdaQueryServer {
  val logger : Logger = CreateLogger(classOf[lambdaQueryServer])
  val config: Config = ObtainConfigReference("lambdaInvoke") match {
    case Some(value) => value
    case None => throw new RuntimeException("Unable to get configuration")
  }
  val lambdaConfig: Config = config.getConfig("lambdaInvoke")
  val configPort: Int = lambdaQueryServer.lambdaConfig.getInt("port")
  logger.info("Loaded configuration success")
  def main(args: Array[String]): Unit = {
    val server = new lambdaQueryServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }
  private val port = configPort
}

class lambdaQueryServer(executionContext: ExecutionContext) {self =>
  private[this] var server: Server = null

  private def start(): Unit = {
    server = ServerBuilder.forPort(lambdaQueryServer.port).addService(lambdaQueryGrpc.bindService(new loqQueryImpl, executionContext)).build.start
    lambdaQueryServer.logger.info("Server has stated. Listening on port: " + lambdaQueryServer.port)
    sys.addShutdownHook{
      lambdaQueryServer.logger.info("Server is shutting down.")
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }
  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }
  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }
}
class loqQueryImpl extends lambdaQueryGrpc.lambdaQuery {
  override def checkLogBool(req: lambdaInvoke) ={
    lambdaQueryServer.logger.info("Sending HTTP request to API Gateway with paramters of lower time interval: " + req.lowerDate + " & upper time interval: " + req.upperDate)
    val configUri = lambdaQueryServer.lambdaConfig.getString("lambdaAPI")
    val invoke: Response = requests.get(configUri, params = Map("lower" -> req.lowerDate, "upper" -> req.upperDate))
    lambdaQueryServer.logger.info("Response code from the lambda was: " + invoke.statusCode)
    val responseText = invoke.text()
    val response = lambdaResponse(message = "Lamba invoke return: " + responseText)
    Future.successful(response)
  }

}