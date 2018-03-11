# College Basketball Coach

Attempt at an Android game that simulates the experience of being the head coach of a college basketball team.

Users will get to manage their team's roster, recruiting, strategy, staff, and training. All of the features have been added in some form.

List of improvments to be made for the user:
- [ ] Games are simulated on a play-by-play basis. The user is able to call timeouts, adjust strategy sliders,
make subsitutions, give team talks (pre-game, during the game, half time, and post-game).
- [ ] Players have a condition attribute that degrades while playing and recoveres while on the bench. Other attributes should degrade as condition gets lower. The player's condition will take a few days to fully recover after a game.
- [ ] Players have a morale attribute that affects their performance in games and training. Morale lowers with bad loses and losing streaks, stays the same with expected loses or wins, and increases with good wins and win streaks.
- [ ] Players (and recruits) have a work ethic attribute. It is their potential to improve their other abilities. Also high work ethic should result in a lower impact for low morale.
- [ ] Games are seperated by time. The sim goes day-to-day so the user can adjust the intensity of practices (etc.) to get players recovered in time for next game.
- [ ] During off-season, the user can schedule non-conference oppenents. Who the user can schedule and where the game is played should depend upon the school's budget and prestige or overall rating.
- [ ] A player's improvement is dependent upon the coaching staff, the quality of oppenents played, and minuted played.
- [ ] More immersive schools: scheduling budget (does the team need to play at bigger oppenents in buy games or can they play home-and-homes with similar sized schools or maybe even schedule non-D1s), recruiting budget (how many players can the user recruit at once), coaching budget (how many coaches the player can have on staff), player budget (how many scholarships does the user have to offer). These budgets can change based the team's performance year-to-year.
- [ ] Recruiting should work like this. Assign a coach to recruit a player. Coaches have a recruiting ability. The higher the ability, the more likely the player will be to gain interest in the team. The player's interest also can change based on how the team performs based on expectations. The user can also make promises (playing time, starter status, team success) to the recruit to try to increase interest. Failure to keep these promises can result in the player transfering. Once a recruit reaches a certain level of interest, the user can offer the recruit a scholarship. Other recruits should react to the offer (positively if they want to play with said player and negatively if they have the same position).
- [ ] During the off-season, the user can cut players (cutting a player with a scholarship should result in a massive hit to the next season's recruits interst).
- [x] The user should be able to view other teams and conferences (but not make changes).
- [ ] Add a button to return to the player's own team.
- [x] Attribute masking. Star ratings instead of actual numeric values.
- [ ] Improved player generation. Attributes should depend on the player's position.
- [ ] Individualize training. Set each player to train as a certain position and with a certain focus.
- [ ] Conference realignment
- [ ] Coaching changes and ability for the user to be hired by other schools or fired by their own
- [ ] Promotion / Relegation game mode
- [ ] An actual, full-on basketball sim like in FM


List of improvments to be made for the AI:
- [ ] AI will adjust starting lineups based on player's ability.
- [ ] AI teams will have a randomly (maybe based on coaching and player abilites) set strategy and practice plan.


Ideas for Play-by-Play Basketball Sim:
* A player's ability depends on their position and the opponent's player at the same position
* Condition affects ability in a log fashon (100-~85 is fairly similar, but steep drop in performace after).
* Big Question: How to deciede which player makes a play and what the play is?


Current issues that need to be addressed:
- [ ] The number of clicks it takes of the sim game button during tournaments before it changes to start new season. Once the tournament is over, the button should change.
- [ ] When the user's team's season is over, the sim next button should sim the rest of the season.
- [ ] The button on the schedule fragment should update when the fragment loads.