# Burp DOM Scanner
It's a Burp Suite's extension to allow for recursive crawling and scanning of Single Page Applications.  
It runs a Chromium browser to scan the webpage for DOM-based XSS.  
It can also collect all the requests (XHR, fetch, websockets, etc) issued during the crawling allowing them to be forwarded to Burp's Proxy, Repeater and Intruder.  
  
It requires node and [DOMDig](https://github.com/fcavallarin/domdig).

## Download
Latest release can be downloaded [here](https://github.com/fcavallarin/burp-dom-scanner/releases/latest/download/burp-dom-scanner.jar)
## Some screenshots

![Burp DOM Scanner Screenshots](https://htcrawl.org/img/burp-dom-scanner-all.png)
