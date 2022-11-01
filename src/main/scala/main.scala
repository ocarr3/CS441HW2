import HelperUtils.CreateLogger
import io.grpc.ManagedChannelBuilder
import com.hellogrpc.hello.{lambdaInvoke, lambdaQueryGrpc, lambdaResponse}
import com.typesafe.config.Config
import lambdaQueryServer.config
import org.slf4j.Logger
// Client for the ScalaPB gRPC server that communicates using protobufs
// Opens on the address and port from the configuration file
// Payload for messages that are used to invoke the lambda function are given are arguments
// Server then sends back the response from the lambda function for viewing

def main(args: Array[String]): Unit = {
  val lambdaConfig: Config = config.getConfig("lambdaInvoke")
  val configPort = lambdaQueryServer.lambdaConfig.getInt("port")
  val configAddress = lambdaQueryServer.lambdaConfig.getString("address")
  val logger : Logger = CreateLogger(classOf[main])

  logger.info("Invoking lambda API Gateway using timestamps given as arguments. They must be of HH:MM:SS.SSS format.")
  if (args.length == 0) {
    logger.error("No arguments given! Please run again")
  }
  logger.info("Starting client for server at " + configAddress + " " + configAddress)
  val channel = ManagedChannelBuilder.forAddress(configAddress, configPort).usePlaintext().build
  val request = lambdaInvoke(lowerDate = args(0), upperDate = args(1))
  val blockingStub = lambdaQueryGrpc.blockingStub((channel))
  val reply: lambdaResponse=blockingStub.checkLogBool(request)
  logger.info(reply.message)
}