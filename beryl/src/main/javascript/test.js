var user = require('./User.js').User('localhost',8080)
   ,group = require('./Group.js').Group('localhost',8080)
   ,events = require('events')
   ,emitter = new events.EventEmitter();
   
//login
user.login(process.env.YSI_EMAIL,process.env.YSI_PASSWD,function (err,reply) {
  if ( err ) {
    console.log('error calling user.login():'+err);
    return;
  }
  console.log('successfully logged in!');
  console.log(reply);
  emitter.emit('login_done',reply);
});

emitter.on('login_done',function(logindata) {
  group.setAuthToken(logindata.authToken);
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
