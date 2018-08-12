var http = require('http');
var fs = require('fs');
var sys = require('sys');
var exec = require('child_process').exec;
var url = require('url');

var server = http.createServer(function(req, res) {
    var query = url.parse(req.url,true).query;
    if(query.url != undefined) {
	   res.end("Rest PI-API activated, playing now " + query.url);
	   playSimpleUrl(query.url);
    } else if(query.extracturl != undefined) {
	   res.end("Rest PI-API activated, extracting now " + query.extracturl + " and playing afterwards");
	   playAndExtractUrl(query.extracturl)
    } else if(query.performaction != undefined) {
        res.end("Performing action: " + query.performaction);
        performAction(query.performaction);
    }

    fs.readFile('./index.html', 'utf-8', function(error, content) {
       	res.writeHead(200, {"Content-Type": "text/html"});
        res.end(content);
    });

});


var io = require('socket.io').listen(server);

io.sockets.on('connection', function (socket, pseudo) {

    socket.on('url', function(url_read) {
       	  socket.url = url_read;
 	      exec("omxplayer  -b $(youtube-dl -g -f mp4 '" + socket.url + "') &");
	});

	socket.on('url_ytdl', function(url_read) {
          socket.url = url_read;
          playAndExtractUrl(socket.url);
    });

	socket.on('url_nyt', function(url_read_nyt) {
          socket.url = url_read_nyt;
          playSimpleUrl(socket.url);
    });

	socket.on('kill', function(kill_omx) {
          actionKill();
    });
});

function performAction(actionType) {
    if(actionType == "kill") {
        actionKill();
    }
    
    if(actionType == "volumedown") {
        volumeControl("-");
    }
    
    if(actionType == "volumeup") {
        volumeControl("+");
    }
    
    if(actionType == "pause") {
        actionPause();
    }
}

function actionPause() {
    writeToCommandCache("p")
}

function volumeControl(percentage) {
    writeToCommandCache(percentage);
}

function actionKill() {
    writeToCommandCache("q");
    exec("killall omxplayer.bin");
}

function writeToCommandCache(cmdKey) {
    exec("echo -n " +cmdKey+ " > /tmp/cmd");
}

function playAndExtractUrl(urlToExtract) {
	playSimpleUrl("$(youtube-dl -g '" + urlToExtract + "')");
}

function playSimpleUrl(url) {
    exec("rm /tmp/cmd"); //reset command cache
    exec("mkfifo /tmp/cmd"); //create new command cache 
    
	exec("omxplayer -b " + url + " < /tmp/cmd");
    exec("echo . > /tmp/cmd"); //Start omxplayer running as the command will initial wait for input via the fifo
}

server.listen(8080);
