package codecheck.github.app

import org.json4s._
import org.json4s.jackson.JsonMethods

import codecheck.github.models.Label
import codecheck.github.models.LabelInput

import scala.concurrent.ExecutionContext.Implicits.global
import java.io.File

trait CreateLabels extends Command {

  def createLabels(owner: String, repo: String, file: File) = {
    val rapi = api.repositoryAPI(owner, repo)

    def doCreateLabel(label: Option[Label], input: LabelInput): Unit = {
      label match {
        case Some(l) if (l.color == input.color) =>
          println(s"Skip create label ${input.name}")
        case Some(l) =>
          rapi.updateLabelDef(input.name, input)
          println(s"Update label ${input.name}")
        case None =>
          rapi.createLabelDef(input)
          println(s"Create label ${input.name}")
      }
    }
    val json = JsonMethods.parse(file)
    val items = (json match {
      case JArray(list) => list
      case JObject => List(json)
      case _ => throw new IllegalArgumentException(JsonMethods.pretty(json))
    }).map(v => LabelInput(
      (v \ "name").extract[String],
      (v \ "color").extract[String]
    ))
    rapi.listLabelDefs.map { labels =>
      items.foreach { input =>
        doCreateLabel(labels.find(_.name == input.name), input)
      }
      done
    }
  }
}