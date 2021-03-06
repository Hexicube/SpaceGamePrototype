importClass(org.tilegames.hexicube.spacegame.Game);
body = Game.controlledBody("test", "Player 1", 1, [0.0, 0.7, 0.0, 1.0], 25000, "player1control", 1, 0.01, "self");
body2 = Game.controlledBody("shield", "Player 1", 1, [1.0, 1.0, 1.0, 1.0], 1000000, null, 0, 1, body);
Game.setBodyPosRotation(body2, 10, 0, Math.PI/2);
Game.destroyWhenDead(body2);
Game.attach(body, body2, 100000000);
Game.requiresParent(body2, body);
body2 = Game.controlledBody("shield", "Player 1", 1, [1.0, 1.0, 1.0, 1.0], 1000000, null, 0, 1, body);
Game.setBodyPosRotation(body2, -10, 0, Math.PI/2);
Game.destroyWhenDead(body2);
Game.attach(body, body2, 100000000);
Game.requiresParent(body2, body);
Game.setBodyPosRotation(body, -250, 0, -Math.PI/2);
body = Game.controlledBody("test", "Player 2", 1, [0.0, 0.6, 1.0, 1.0], 25000, "player2control", 1, 0.01, "self");
body2 = Game.controlledBody("shield", "Player 1", 1, [1.0, 1.0, 1.0, 1.0], 1000000, null, 0, 1, body);
Game.setBodyPosRotation(body2, -10, 0, Math.PI/2);
Game.destroyWhenDead(body2);
Game.attach(body, body2, 100000000);
Game.requiresParent(body2, body);
body2 = Game.controlledBody("shield", "Player 1", 1, [1.0, 1.0, 1.0, 1.0], 1000000, null, 0, 1, body);
Game.setBodyPosRotation(body2, 10, 0, Math.PI/2);
Game.destroyWhenDead(body2);
Game.attach(body, body2, 100000000);
Game.requiresParent(body2, body);
Game.setBodyPosRotation(body, 250, 0, Math.PI/2);
//Game.livingBody("bigwall", 30000000, [1, 0, 0, 1], 0, 1);