package codecheck.github.operations

import java.net.URLEncoder
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.models.Label

trait LabelOp {
  self: GitHubAPI =>

  private def doLabels(method: String, owner: String, repo: String, number: Long, labels: Seq[String]): Future[List[Label]] = {
    val path = s"/repos/$owner/$repo/issues/$number/labels"
    val body = if (method == "GET") {
      JNothing
    } else {
      JArray(labels.map(JString(_)).toList)
    }
    exec(method, path, body).map {
      _.body match {
        case JArray(arr) => arr.map(new Label(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def addLabels(owner: String, repo: String, number: Long, labels: String*): Future[List[Label]] = {
    doLabels("POST", owner, repo, number, labels)
  }

  def replaceLabels(owner: String, repo: String, number: Long, labels: String*): Future[List[Label]] = {
    doLabels("PUT", owner, repo, number, labels)
  }

  def removeAllLabels(owner: String, repo: String, number: Long): Future[List[Label]] = {
    doLabels("PUT", owner, repo, number, Nil)
  }

  def removeLabels(owner: String, repo: String, number: Long, label: String): Future[List[Label]] = {
    val path = s"/repos/$owner/$repo/issues/$number/labels/" + URLEncoder.encode(label, "utf-8").replaceAll("\\+", "%20")
    exec("DELETE", path).map {
      _.body match {
        case JArray(arr) => arr.map(new Label(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def listLabels(owner: String, repo: String, number: Long): Future[List[Label]] = {
    doLabels("GET", owner, repo, number, Nil)
  }

  def listLabelDefs(owner: String, repo: String): Future[List[Label]] = {
    throw new UnsupportedOperationException()
  }

  def getLabelDef(owner: String, repo: String, label: String): Future[Label] = {
    throw new UnsupportedOperationException()
  }

  def createLabelDef(owner: String, repo: String, label: Label): Future[Label] = {
    throw new UnsupportedOperationException()
  }

  def updateLabelDef(owner: String, repo: String, name: String, label: Label): Future[Label] = {
    throw new UnsupportedOperationException()
  }

  def removeLabelDef(owner: String, repo: String, label: String): Future[Boolean] = {
    throw new UnsupportedOperationException()
  }
}
