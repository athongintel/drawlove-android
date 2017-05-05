var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var MessageSchema = Schema(
    {
        contentType: {type: Number, require: true},
        contentMetaData: {type: String},
        content: {type: String, required: true},
        sender: {type: Schema.ObjectId, ref: 'User', required: true},
        group: {type: Schema.ObjectId, ref: 'Group', required: true},
        sentDate: {type: Date, default: Date.now},
    },
    {
        collection: "tbMessage"
    }
);

module.exports = mongoose.model('Message', MessageSchema);