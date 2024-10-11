@echo off
java -Dserver.port=9091 -Dcsp.sentinel.dashboard.server=localhost:8090 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.8.jar

timeout /t 10 /nobreak
start http://localhost:9091
