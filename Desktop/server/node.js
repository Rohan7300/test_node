var http = require("http");
var dt = require('./date')
var fs = require('fs')
var url = require('url');
const { text } = require("stream/consumers");


/*http.createServer(function(req,res){
    res.writeHead(200, {"content-type": 'text/html'})
    res.write("the time is:" + dt.myDate());
    res.end()
}).listen(8080);*/

http.createServer((req,res)=>{
    var q = url.parse(req.url, true)
    var filename = "." + q.pathname
    fs.readFile(filename, function(err,data){
        if(err){
            res.writeHead(404,{'content-type': 'text/html'});
            return res.end("File is not available")
        }
        else{
            res.writeHead(200,{'content-type': 'text/html'})
            res.write(data)
            return res.end()
        }
    })


}).listen(8080)