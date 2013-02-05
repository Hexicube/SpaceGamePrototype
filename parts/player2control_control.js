//forward, back, left, right, fire
keysdown = Game.scriptGet(scriptid, "keysdown");
if(pressedkey == 19) keysdown[0] = waspress;
else if(pressedkey == 20) keysdown[1] = waspress;
else if(pressedkey == 21) keysdown[2] = waspress;
else if(pressedkey == 22) keysdown[3] = waspress;
else if(pressedkey == 60) keysdown[4] = waspress;
Game.scriptSet(scriptid, "keysdown", keysdown);