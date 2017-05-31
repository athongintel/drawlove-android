var nodemailer = require('nodemailer');
var randomstring = require('randomstring');
var crypto = require('crypto');
var config = require('../config.js');

var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'immortplanet@gmail.com',
        pass: 'vinamilk'
    }
});

var mailOptions = {
    from: 'DrawLove no-reply@drawlove.immortplanet.com',
    subject: 'DrawLove email verification',
};

var MailServices = {
	sendVerificationEmail: function(user, cb){
		var random = randomstring.generate(16);
		var hash = crypto.createHash('sha256');
		hash.update(random);
		hash.update(String(user._id));
		var token = hash.digest('base64');
		token = encodeURIComponent(token);
		mailOptions["to"] = user.email;
		mailOptions["html"] = "Welcome to DrawLove. Please click on the link below to finish registration progress. </br>"
		mailOptions["html"] += '<a href="' + config.host + "/api/email_verification?userID=" + String(user._id) + "&token=" + token + '">Finish your account creation</a>';
		transporter.sendMail(mailOptions, function(err, info){
			cb(err, random);
		});
	},

	verifyEmail: function(user, uToken){
		var hash = crypto.createHash('sha256');
		hash.update(user.emailToken);
		hash.update(String(user._id));
		var token = hash.digest('base64');
		console.log("calculated token: ", token);
		console.log("uToken", uToken);
		return token == uToken;
	}
}

module.exports = MailServices;