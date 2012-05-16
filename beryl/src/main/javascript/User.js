var http = require('http');

var User = function(host,port) {
  var authToken = null;
  return {
    login: function(email, password, callback) {
      var data = 'email='+email+'&password='+password;
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
          console.log(body);
          json = JSON.parse(body);
          if ( res.statusCode/100 != 2 ) callback(json);
          else {
            authToken = json.user.authToken;
            callback(null,json);
          }
        });
      });
      req.write(data);
      req.end();
    }
  }
};

exports.User = User;