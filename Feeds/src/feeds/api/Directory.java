/*
 * Directory.java
 *
 * Created on 30 de Setembro de 2000, 1:26
 */

package feeds.api;

/**
 * The ChannelDirectory interface provides applications with the means of
 * getting handles to existing event channels or creating new ones.
 * 
 * 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */
public interface Directory {

	/**
	 * Searches the global event channel directory for an event channel matching
	 * the given name. The operation will fail if the search does not produce a
	 * result in the allowed time interval.
	 * 
	 * @return a reference to an {@link deeds.api.core.Channel} object
	 *         corresponding to the intended event channel.
	 * @param name
	 *            the name of the intended event channel.
	 * @throws deeds.api.core.DeedsException
	 *             if the underlying system is unable to resolve the channel
	 *             name.
	 */
	public <T> T lookup(String name, Object... extraArgs) throws FeedsException;

	/**
	 * Attempts to create a new event channel given an existing template. If the
	 * operation succeeds, the newly created event channel is added to the event
	 * channel directory. From then on it can be the subject of lookup and
	 * subsequent operations, until the expiration deadline is reached.
	 * 
	 * Due to the asynchronous nature of the underlying runtime support in some
	 * situations there is some chance that the actual result of the operation
	 * may be incorrectly reported.
	 * 
	 * Expired event channels are silently discarded.
	 * 
	 * @param template
	 *            Name of the event channel template from which the event
	 *            channel will be cloned.
	 * @param name
	 *            Name of the desired event channel.
	 * @param lifeSpan
	 *            Lifespan of the event channel in minutes.
	 * @throws DeedsException
	 *             whenever the operation fails for some reason. Some of the
	 *             possible failures are:
	 * 
	 *             - Name collision: the desired name is already in use.
	 * 
	 *             - Template unknown: the source template does not exist, or a
	 *             description was not received in the allowed time interval.
	 * 
	 *             - General operation timeouts due to communication problems or
	 *             unreachable servers.
	 */
	public <T> T clone(String template, String name, Object... extra_args) throws FeedsException;

}