package de.digitalstep.ntlmproxy

import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.response.Response
import java.net.URL

object BigBeeExample1 extends App {
          val httpClient = new HttpClient
          val response: Response = httpClient.get(new URL("http://www.google.com/"))
          println(response.status)
          println(response.body.asString)
}