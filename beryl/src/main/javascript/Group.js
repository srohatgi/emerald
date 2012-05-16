var http = require('http');

var Group = function(host,port) {
  var authToken = null;
  return {
    browse: function(prev,next,count, callback) {
      var options = {
        host:host,
        port:port,
        path:'/net/groups?count='+count,
        method:'GET',
        headers: { 
          'Content-Type': 'application/x-www-form-urlencoded',
          'Accept': '*/*',
          'X-Auth-Token': authToken
        }
      };
      var req = http.request(options,function(res) {
        var body = '';
        res.on('data',function (chunk) { body+=chunk; });
        res.on('end',function() {
          var json = JSON.parse(body);
          if ( res.statusCode/100 != 2 ) callback(json);
          else {
            callback(null,json);
          }
        });
      });
      req.end();
    },
    setAuthToken: function(token) {
      authToken = token;
    }
  }
};

exports.Group = Group;