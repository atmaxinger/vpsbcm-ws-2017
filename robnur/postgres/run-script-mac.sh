#!/bin/sh 
osascript <<END 
tell application "Terminal"
    do script "cd \"`pwd`\";$1;exit"
end tell
END
