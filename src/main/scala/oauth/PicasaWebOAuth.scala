package org.bone.sphotoapi.oauth

import org.scribe.oauth.OAuthService
import org.scribe.model.Token
import org.scribe.model.Verb

import java.util.Date

import scala.util.Try
import scala.xml.Node
import scala.xml.XML

class PicasaWebOAuth(override val appKey: String, override val appSecret: String,
                 override protected[sphotoapi] val service: OAuthService,
                 override protected[sphotoapi] var accessToken: Option[Token] = None, 
                 override protected[sphotoapi] var refreshToken: Option[String] = None,
                 override protected[sphotoapi] var expireAt: Date = new Date) extends OAuth
{

  protected val refreshURL = "https://accounts.google.com/o/oauth2/token"
  protected val prefixURL = "https://picasaweb.google.com/data/feed/api/"

  /**
   *  Send Request and Parse Response to JSON / XML
   *  
   *  @param    url       The API endpoint URL
   *  @param    verb      The HTTP request method
   *  @param    params    The parameters to API method
   *  @return             The XML node or JSON object if successfully called API.
   */
  def sendRequest(url: String, verb: Verb, 
                  params: (String, String)*): Try[Node] = 
  {

    def parseResponse(body: String) = Try {
      try {
        XML.loadString(body)
      } catch {
        case e: Exception => throw new Exception(body)
      }
    }

    for {
      (code, contentType, body) <- sendRequest_(url, verb, params: _*)
      response <- parseResponse(body)
    } yield response

  }
}

