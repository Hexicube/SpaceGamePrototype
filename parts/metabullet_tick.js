used = Game.scriptGet(scriptid, "used");
if(used == false)
{
	timer = Game.scriptGet(scriptid, "timer");
	rotimpulse += 3000;
    if(timer > 0) timer--;
    if(timer <= 0)
    {
        timer = 1;
        Game.destroyWhenDead(Game.fire(body, "bigbullet", null, null, 0, 5, 0, 0, 1, 5000));
        Game.destroyWhenDead(Game.fire(body, "bigbullet", null, null, Math.PI, 5, 0, 0, 1, 5000));
        Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI*1.5, 5, 0, 0, 1, 5000));
        Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI*0.5, 5, 0, 0, 1, 5000));
    	count = Number(Game.scriptGet(scriptid, "counter")) + 1;
		if(count >= 20)
		{
			health = 0;
		}
		else Game.scriptSet(scriptid, "counter", count);
    }
    Game.scriptSet(scriptid, "timer", timer);
    Game.scriptSet(scriptid, "used", true);
}