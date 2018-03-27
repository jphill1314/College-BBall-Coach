# College Basketball Coach

Attempt at an Android game that simulates the experience of being the head coach of a college basketball team.

Users will get to manage their team's roster, recruiting, strategy, staff, and training. All of the features have been added in some form.

List of improvements to add (for version 1.0):
* Roster
- [x] Set starting lineup

* Schedule
- [x] Non-conference games
- [ ] Games are seperated by time -> in between teams can practice + players' recover from games (after 1.0)
- [x] When the player has no games left, it should only take one press of the sim button to simulate the rest of the season
- [x] The button should show the proper next action when the fragment loads (ie will show start tournament when it's tournament time)

* Standings
- [x] Sort by win % first and # of wins second
- [x] Show rankings and RPI (or something like strength of schedule)
- [x] Show the correct season years

* Recruiting
- [x] Assign a coach to recruit a certain player instead of pressing a button after every game
- [x] Recruits that are not recruited will gradually lose interest
- [x] Recruits that are recruited will gain interest based on the coach's ability and the team's results
- [ ] Each recruit has a scouting report that is gradually revealed the longer they are recruited (probably best and worst abilities and work ethic)
- [x] Once a recruit reaches a certain level of interest, he will have a chance to commit after every game
- [ ] When a recruit commits, all other recruits' interest is effected based on the position of the commit and the number of spots left on the team
- [x] The recruits should be generated based on the team's expected needs and their current ability, but there should be a couple of hard to get players that are much better

* Strategy
- [ ] Add ability to press with varring intensity (to slow the pace or to steal the ball) and frequency (never, sometimes, always)
- [x] Add ability to intentionally foul
- [x] Add ability to set overal aggression level (lower means fewer fouls, but not as good defense)

* Staff
- [x] Add recruiting ability
- [ ] Hire and fire staff

* Training
- [ ] Add intensity slider which affects how much the players improve and how quickly they recover from games (after 1.0)
- [x] Train each position differently
- [x] A player's improvement depends on playing time, practice intensity, work ethic, and the coaches' abilities

* Game Viewer
- [x] Add empty list items so that the FAB doesn't always block bottom item in lists
- [x] Indicate which team has the ball in the Play-by-Play screen
- [ ] Add more varriety to team talks
- [x] Give better indication of a player's rating, prefered position, condition, and if they are a pending sub on roster page
- [x] Add additional screen on roster page to get a better view of stats
- [x] Add team stats
- [ ] (maybe 1.0) Add ability to 'run a play' out of a timeout in close and late situations
- [x] Prevent the user from accessing other parts of the game while in the game view
- [ ] If the user quits while in game, reopening the app should place the user back into the game
- [x] Add ability to pause the sim

* Game Simulation
- [ ] Save games that are quit while in progress
- [ ] Prevent a player that is shooting free throws from being subbed out if they don't make their final free throw
- [ ] Find better balance between which plays occur
- [ ] Improve balance of gaining fatigue vs losing fatigue
- [ ] Have the AI modify strategy based on the situation
- [x] Track more stats like steals and turnovers

* Players
- [x] Save game stats
- [x] Generate initial abilities based on position
- [x] Calculate overall rating based on position 
- [x] A player can be set to train as a different position, but will improve slower than if they trained as their best position

* Offseason (Pushed to after 1.0)
- [ ] Team has a maximum and minimum size. The user can cut players if they have too many and can add walk-ons to reach the minimum
- [ ] The user can schedule non-conference games or let the computer pick (Maybe generate an initial schedule that the user can modify if they wish)
- [ ] Generate other teams' schedules after the player creates theirs

* General UI
- [ ] Add a team overview screen
- [x] Add a button that will take the user to the overview screen from anywhere in the game (except while in game)[This is effectively added)
- [x] Indicate which team is being viewed
- [x] Add a start screen from which the user can load a save or start a new one (there can only ever be one saved game [for now])
- [x] Add a team creation page for starting a new game
- [ ] Add loading indicators whenever an AsyncTask is running

* Team Creation
- [x] User can name the school, the mascot, the conference, and the head coach's name
- [x] User can choose the average rating of the other schools in the conference and their own team's overall rating
- [ ] There should be a couple of pre-made options (maybe later - think scenarios)

* Teams and Conferences
- [x] Aim for 100 teams in at least 10 conferences
- [x] National Championship should take all conference champs + some at large teams based on their RPI (16 or 32 teams?)
- [x] Add tournament overview view for both conference tournaments and national championship
