var http = require('http');

var User = function(host,port) {
  return {
    login: function(email, password, callback) {
      data = 'email='+email+'&password='+password;
      var options = {
        host:host,
        port:port,
        path:'/net/users/login',
        method:'POST',
        headers: { 
          'Content-Type': 'application/x-www-form-urlencoded',
          'Accept': '*/*'
        }
      };
      var req = http.request(options,function(res) {
        var body = '';
        res.on('data',function (chunk) { body+=chunk; });
        res.on('end',function() {
          if ( res.statusCode/100 != 2 ) callback(body);
          else {
            callback(null,body);
          }
        });
      });
      req.write(data);
      req.end();
    }
  }
};

exports.User = User;