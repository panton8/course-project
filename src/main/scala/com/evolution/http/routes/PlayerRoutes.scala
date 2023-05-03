package com.evolution.http.routes

import cats.effect._
import com.evolution.domain.{Club, GameWeek, Id, Name, Position, Statistic, Surname}
import com.evolution.http.domain.PlayerStatistics
import com.evolution.service.PlayerService
import org.http4s.circe._
import org.http4s._
import io.circe.syntax._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._

object PlayerRoutes {

  def routes(playerService: PlayerService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "players" =>
        playerService.showListOfPlayers().flatMap(players => Ok(players.asJson))

      case GET -> Root / "players" / IntVar(id) =>
        playerService.findById(Id(id)) flatMap {
          case Some(player) => Ok(player.asJson)
          case None         => NotFound()
        }

      case GET -> Root / "players" / "club" / club =>
        playerService.showListOfPlayersByClub(Club.withName(club)).flatMap(players => Ok(players))

      case GET -> Root / "players" / "position" / position =>
        playerService.showListOfPlayersByPosition(Position.withName(position)).flatMap(players => Ok(players))

      case GET -> Root / "players" / name / surname =>
        playerService.findByName(Name(name), Surname(surname)).flatMap(player => Ok(player))

      case GET -> Root / "players" / IntVar(id) / "statistics" / "total" =>
        playerService.takeTotalStatistic(Id(id)).flatMap(statistics => Ok(statistics))

      case GET -> Root / "players" / IntVar(id) / "statistics" / "week" / IntVar(week) =>
        playerService.takeWeekStatistic(Id(id), GameWeek(week)).flatMap(statistics => Ok(statistics))

      case GET -> Root / "players" / IntVar(id) / "points" / "total" =>
        playerService.giveTotalPoints(Id(id)).flatMap(points => Ok(points))

      case GET -> Root / "players" / IntVar(id) / "points" / "week" / IntVar(week) =>
        playerService.givePointsByWeek(Id(id), GameWeek(week)).flatMap(points => Ok(points))

      case PUT -> Root / "players"/ IntVar(id) / "recovered" / "injured" =>
          playerService.getInjured(Id(id)).flatMap(upd => Ok())

      case PUT -> Root / "players" / IntVar(id) / "recovered" / "recovered" =>
        playerService.getRecovered(Id(id)).flatMap(upd => Ok())

      case req @ POST -> Root / "players" / "statistics" / "matchweek" =>
        for {
          userReg <- req.as[PlayerStatistics]
          plStat <- playerService.addMatchActions(
            userReg.playerId,
            Statistic(
              userReg.goals,
              userReg.assists,
              userReg.minutes,
              userReg.ownGoals,
              userReg.yellowCard,
              userReg.redCard,
              userReg.saves,
              userReg.cleanSheet
            ),
            userReg.gameWeek
          )
          response <- plStat match {
            case Some(_) => Ok()
            case None    => BadRequest()
          }
        } yield response
    }
  }
}
