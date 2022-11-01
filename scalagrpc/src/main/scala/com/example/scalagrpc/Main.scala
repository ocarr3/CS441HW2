package com.example.scalagrpc

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    ScalagrpcServer.stream[IO].compile.drain
