var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var UserSchema = Schema(
    {
        chatID: {type: String, required: true, unique: true},
        passwordHash: {type: String, required: true},
        salt: {type: String, required: true},
        email: {type: String, required: true},
        emailToken: {type: String},
        isActivated: {type: Boolean},
        friends : [{type: Schema.ObjectId, ref: 'User'}]
    },
    {
        collection: "tbUser"
    }
);

module.exports = mongoose.model('User', UserSchema);