var user = require('./User.js').User('localhost',8080)
   ,group = require('./Group.js')
   ,events = require('events')
   ,emitter = new events.EventEmitter();
   
//login
user.login('sumeet.rohatgi@hotmail.com','test12',function (err,reply) {
  if ( err ) {
    console.log('error calling user.login():'+err);
    return;
  }
  console.log('successfully logged in!');
  console.log(reply);
  emitter.emit('login_done',reply);
});

emitter.on('login_done',function(logindata) {
  group.authToken = logindata.authToken;
  group.browse(null,null,10,function(err,reply) {
    if ( err ) {
      console.log('error calling user.login()');
      return;
    }
    reply.authToken = logindata.authToken;
    console.log('successfully browsed groups!');
    console.log(reply);
    emitter.emit('browse_done',reply);
  });
});
