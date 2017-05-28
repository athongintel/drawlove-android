var UserServices = require('./UserServices.js');

const MESSAGE = 'message';

var socketIO;
var sockets = {};

var SocketServices = {

	init: function(io){
		socketIO = io;

		io.on('connection', function(socket){
			socket.on(MESSAGE, function(obj){
				//-- check all logged in user
				console.log(obj);
				var event = obj['event'];
				var data = JSON.parse(obj['data']);
				switch (true){
					case event == "login":{
							var user = UserServices.activeUsers[data.userID];
							if (user && (user.ioSocketToken == data.ioSocketToken)){
								//-- socket OK, send response
								socket.emit(MESSAGE, "login", {status : "success"});
								sockets[data.userID] = socket;
							}
							else{
								socket.emit(MESSAGE, "login", {status: "failed"});
							}
						}
						break;

					default:
						break;
				}
			});
		});
	}
}

module.exports = SocketServices;