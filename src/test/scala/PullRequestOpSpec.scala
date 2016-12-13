import org.scalatest.FunSpec
import scala.concurrent.Await

import codecheck.github.models.IssueState
import codecheck.github.models.PullRequestInput
import java.util.Date

class PullRequestOpSpec extends FunSpec with Constants {

  describe("listPullRequests") {
    it("with valid repo should succeed") {
      val list = Await.result(api.listPullRequests(otherUser, otherUserRepo), TIMEOUT)
      assert(list.length >= 0)
      assert(list.exists(_.state == IssueState.open))
      assert(list.exists(_.base.repo.full_name == s"$otherUser/$otherUserRepo"))
      assert(list.exists(_.base.user.login == otherUser))
      assert(list.exists(_.base.repo.name == otherUserRepo))
    }
  }

  describe("createPullRequest(owner, repo, input)") {
    val username = otherUser
    val reponame = otherUserRepo

    it("should success create and close") {
      val title = "Test Pull Request " + new Date().toString()
      val input = PullRequestInput(title, "githubapi-test-pr", "master", Some("PullRequest body"))
      val result = Await.result(api.createPullRequest(username, reponame, input), TIMEOUT)
      assert(result.title == title)
      assert(result.state == IssueState.open)

      val result2 = Await.result(api.closePullRequest(username, reponame, result.number), TIMEOUT)
      assert(result2.title == title)
      assert(result2.state == IssueState.closed)
    }

  }

}
