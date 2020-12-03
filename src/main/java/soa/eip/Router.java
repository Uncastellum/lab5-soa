package soa.eip;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  private class MaxProc implements Processor{

    public void process(Exchange exchange) throws Exception {
      String exch = exchange.getIn().getBody(String.class);
      String body = "", max = null;

      for(String it: exch.split(" ")){
        if(it.matches("max:[0-9]+")){
          max = it.substring(4);
        } else {
          body = body + " " + it;
        }
      }

      if(max != null) body += "?count=" + max;
      
      exchange.getOut().setBody(body);
    }

  }

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      .process(new MaxProc())
      .toD("twitter-search:${body}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
