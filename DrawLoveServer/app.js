var express = require('express');
var mongoose = require('mongoose');
var assert = require('assert');
var session = require('express-session');
var bodyParser  = require('body-parser');
var http = require('http');

var config = require('./config.js');

var app = express();

//-- connect to database
var mongoose = require('mongoose');
mongoose.connect("mongodb://localhost/drawlove-db", function(error){
	if (error){
		console.log('Error: Cannot connect to database. App exits.');
		console.log(error);
		process.exit(1);
	}
});

//-- middleware init
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(session({
	secret: "-this-will-not-be-shown-3141592654-",
	cookie: {maxAge: 600000},
	saveUninitialized: false,
	resave: true
}));

//-- routing
app.use('/', require('./routes/main-routes.js'));
app.use('/user', require('./routes/user-routes.js'));
app.use('/group', require('./routes/group-routes.js'));

var httpServer = http.createServer(app);
httpServer.listen(config.port);
