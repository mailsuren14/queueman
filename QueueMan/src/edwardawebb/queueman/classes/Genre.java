/**
 * QueueMan
 * Sep 24, 2009
 *  http://edwardawebb.com/
 */
package edwardawebb.queueman.classes;

/**
 * @author David Ehringer
 */
public class Genre {

	private long id;
	private String name;

	public Genre(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
