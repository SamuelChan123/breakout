#### High-level design goals of the project
The main high level design goals of our project were to make an interactive application with multiple components that 
the user could interact with that also interacted with each other and implemented inheritance hierarchies when necessary
to minimize repeated code and other redundancies. To do this, we divided the project into several
components. We knew we had to have a main screen that is responsible for actually showing the components that are 
generated, and a separate "play level" class to generate and control the components that would be shown. It would also
be in this class that the user would interact with the different components that were generated. Lastly, we knew that 
the components that were generated themselves should probably be their own classes. Once we finished making
those classes for gameplay, we knew we'd need to generate tests as well, so we made a separate group of classes for
tests.


#### How to add new features to the project
To add new features to our project, we'd most likely add the methods to handle that new feature/event in the play level
class, and if it was a new object entirely, we'd add a new class to the project. If, for instance, it were a powerup,
we wouldn't need to create an entirely new class for it, but rather, write a function for what that powerup would do,
and define a new random number upon a brick breaking for which it would be dropped. However, if we were to add spikes or
some sort of hazard that the ball couldn't touch on certain parts of the screen, that would be something very different
from the components we have now, so we'd make a new class for that object and handle its collision with the ball in the
PlayLevel class. For testing, if we needed to test these new features, we would simply add a Tester for it to check
if the event were fulfilled in the template outlined by the abstract Tester class.


#### Justification of Major Design Choices
We chose to put the functions that generated the game components and the functions responsible for their interactions in
one class since we wanted to follow the model that professor Duval had in his code, where the step function and the 
interactions were all together. We figured this made sense since we wanted to have the interactions in one place rather
than worry about scattering them across the file, because while debugging, we often found that changing one set of 
interactions would cause changes in another, so having them all together let us easily see how they could be related.

For the distinct individual components, like the bricks, the paddle, and the ball, we made them separate classes since
we needed a way for those objects to hold state, like their current position and velocity or brick type, and for that 
state to be modified.

The most significant "con" of this major design choice to make the large play level class is that the class ended up
being very large and contained a lot of code that did many things. If we had to describe what this class did, it would
be something along the lines of "handle all the interactions that could take place between all the objects." This, of
course, meant that the class was too full. However, we decided to keep it this way because once our code was commented
out, we could still clearly track what the different elements of the PlayLevel class did, which was essential to 
figuring out our game play. Moreover, not only was the code readable, but it was also significantly easier to think
through different issues and implement the new features this way, since we didn't need to worry about passing variables
through classes or making sure we were messing other interactions up as well. Rather, all the elements were in front of
us, and so we could clearly show which components would be involved in each step and how without making a reader track
the progression of a single interaction through multiple classes for the multiple components involved.

#### States any assumptions or decisions made to simplify or resolve ambiguities in your the project's functionality
We assumed that if future test cases/levels would be added, they would be made/added in the same order, and the instance
variables in the PlayLevel class that specify levels and the test file would be appropriately updated. Moreover, we also
assumed that our game would always be a two player game, because even if only one person were playing, it would
definitely add an interesting level of difficulty to have a "block" of sorts that the player had to constantly bypass.