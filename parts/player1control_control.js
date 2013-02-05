//forward, back, left, right, fire
keysdown = Game.scriptGet(scriptid, "keysdown");
if(pressedkey == 51) keysdown[0] = waspress;
else if(pressedkey == 47) keysdown[1] = waspress;
else if(pressedkey == 29) keysdown[2] = waspress;
else if(pressedkey == 32) keysdown[3] = waspress;
else if(pressedkey == 129) keysdown[4] = waspress;
Game.scriptSet(scriptid, "keysdown", keysdown);