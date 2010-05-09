package edwardawebb.queueman.queues;
import com.flurry.android.FlurryAgent;

import edwardawebb.queueman.classes.Disc;
import edwardawebb.queueman.classes.Netflix;
import edwardawebb.queueman.classes.NetflixResponse;


public abstract class MutableQueue extends Queue{
	
	protected String eTag;
	
	public MutableQueue(Netflix netflix) {
		super(netflix);
		// TODO Auto-generated constructor stub
	}

	public NetflixResponse postTitle(){
		return null;
		// TODO add implementation and return statement
	}

	public NetflixResponse moveTitle(){
		return null;
		

	}

	public NetflixResponse deleteTitle(){
		return null;
		// TODO add implementation and return statement
	}
	
		/**
	 * @return the eTag
	 */
	public String geteTag() {
		return eTag;
	}

	/**
	 * @param eTag the eTag to set
	 */
	public void seteTag(String eTag) {
		this.eTag = eTag;
	}
	
	
	/**Collection Mutators ********************************************************/
	
	
	


	/**
		 * Moves the disc at the specified queue position to the new position.
		 * Position is 1 based
		 * 
		 * @param oldPosition
		 * @param newPosition
		 */
	public void reorder(int oldPosition, int newPosition) {
		// always good to check to make sure the indices are ok.
		// we are being passed 'postions' 1,2,3....
		// but discs is 0 based, so down shift!
		if (oldPosition >= 1 && newPosition >= 1) {
			if (newPosition > titles.size()) {
				newPosition = titles.size();
			}
			Disc movie = titles.remove(oldPosition - 1);
			titles.add(newPosition - 1, movie);
		} else {
			FlurryAgent.onError("outOfBounds",
					"reorder: provided indices are out of bound. old:"
							+ oldPosition + ", new:" + newPosition,
					"NetFlixQueue");
		}

	}
}
