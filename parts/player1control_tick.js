used = Game.scriptGet(scriptid, "used");
fuel = Game.scriptGet(scriptid, "fuel");
if(used == false)
{
	keysdown = Game.scriptGet(scriptid, "keysdown");
	timer = Game.scriptGet(scriptid, "timer");
	used = true;
	heatGen = 0;
    if(keysdown[0] && fuel > 0)
    {
    	impulse += 500000;
    	heatGen += 12;
    	fuel--;
    }
    if(keysdown[1] && fuel > 0)
    {
    	impulse -= 200000;
    	heatGen += 6;
    	fuel--;
    }
    if(keysdown[2] && fuel > 0)
    {
    	rotimpulse += 30000;
    	heatGen += 2;
    	fuel--;
    }
    if(keysdown[3] && fuel > 0)
    {
    	rotimpulse -= 30000;
    	heatGen += 2;
    	fuel--;
    }
    if(timer > 0) timer--;
    if(keysdown[4] && timer <= 0 && fuel >= 20)
    {
    	heatGen += 55;
        timer = 4;
        Game.destroyWhenDead(Game.fire(body, "bullet", null, null, 0, 14, 5000000, 0, 1, 5000));
        //Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI+0.3, 14, 5000000, 0, 1, 5000));
        //Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI-0.3, 14, 5000000, 0, 1, 5000));
        //Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI+0.6, 14, 5000000, 0, 1, 5000));
        //Game.destroyWhenDead(Game.fire(body, "bullet", null, null, Math.PI-0.6, 14, 5000000, 0, 1, 5000));
        impulse += 2000000;
        fuel -= 5;
    }
    Game.scriptSet(scriptid, "timer", timer);
    Game.scriptSet(scriptid, "used", true);
    heat += heatGen;
    /*if(fuel > 0 && heat > 5000)
    {
    	heat--;
    	fuel--;
    }*/
    if(heat >= 10000)
    {
    	health -= (heat-10000)/400;
    }
    Game.scriptSet(scriptid, "fuel", fuel);
}
Game.print("Player 1 health: "+Math.ceil(health/250)+"%", 20, 20, true, [0.0, 0.7, 0.0, 1.0]);
Game.print("Player 1 heat: "+heat, 20, 40, true, (heat>=10000)?([0.7, 0.0, 0.0, 1.0]):([0.0, 0.7, 0.0, 1.0]));
Game.print("Player 1 fuel: "+Math.round(fuel), 20, 60, true, [0.0, 0.7, 0.0, 1.0]);