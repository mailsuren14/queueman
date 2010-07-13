package edwardawebb.queueman.queues;

import java.net.MalformedURLException;
import java.net.URL;

import edwardawebb.queueman.classes.Netflix;
import edwardawebb.queueman.classes.User;
import edwardawebb.queueman.handlers.DiscQueueHandler;
import edwardawebb.queueman.handlers.QueueHandler;

public class DiscQueue extends MutableQueue{

	public DiscQueue(Netflix netflix) {
		super(netflix);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected QueueHandler getQueueHandler() {
		// TODO Auto-generated method stub
		return new DiscQueueHandler(this);
	}

	@Override
	protected URL getQueueUrl(User user) throws MalformedURLException {
		// TODO Auto-generated method stub
		return new URL(netflix.sign("http://api.netflix.com/users/" + user.getUserId()
				+ "/queues/disc/available" + expanders +  "&start_index=" + startIndex + "&max_results=" + maxTitles));
	}

	@Override
	protected String getPrettyName() {
		// TODO Auto-generated method stub
		return "DVD";
	}




}
