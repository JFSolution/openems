package io.openems.backend.metadata.api;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class Edge {

	private final Logger log = LoggerFactory.getLogger(Edge.class);

	public enum State {
		ACTIVE, INACTIVE, TEST, INSTALLED_ON_STOCK, OFFLINE;
	}

	private final int id;
	private final String apikey;
	private String name;
	private String comment;
	private State state;
	private String version;
	private String producttype;
	private JsonObject jConfig;
	private ZonedDateTime lastMessage = null;
	private ZonedDateTime lastUpdate = null;
	private Integer soc = null;
	private String ipv4 = null;
	private boolean isOnline;

	public Edge(int id, String apikey, String name, String comment, State state, String version, String producttype,
			JsonObject jConfig, Integer soc, String ipv4) {
		this.id = id;
		this.apikey = apikey;
		this.name = name;
		this.comment = comment;
		this.state = state;
		this.version = version;
		this.producttype = producttype;
		this.jConfig = jConfig;
		this.soc = soc;
		this.ipv4 = ipv4;
	}

	public int getId() {
		return id;
	}

	public String getApikey() {
		return apikey;
	}

	public String getName() {
		return name;
	}

	public JsonObject getConfig() {
		return this.jConfig;
	}

	public String getVersion() {
		return version;
	}

	public String getProducttype() {
		return producttype;
	}

	public JsonObject toJsonObject() {
		JsonObject j = new JsonObject();
		j.addProperty("id", this.id);
		j.addProperty("name", this.name);
		j.addProperty("comment", this.comment);
		j.addProperty("version", this.version);
		j.addProperty("producttype", this.producttype);
		j.addProperty("online", this.isOnline);
		return j;
	}

	@Override
	public String toString() {
		return "Edge [id=" + id + ", name=" + name + ", comment=" + comment + ", state=" + state + ", version="
				+ version + ", producttype=" + producttype + ", jConfig="
				+ (jConfig.toString().isEmpty() ? "NOT_SET" : "set") + ", lastMessage=" + lastMessage + ", lastUpdate="
				+ lastUpdate + ", soc=" + soc + ", ipv4=" + ipv4 + ", isOnline=" + isOnline + "]";
	}

	/*
	 * Online
	 */
	private final List<Consumer<Boolean>> onSetOnline = new CopyOnWriteArrayList<>();

	public void onSetOnline(Consumer<Boolean> listener) {
		this.onSetOnline.add(listener);
	}

	public boolean isOnline() {
		return this.isOnline;
	}

	/**
	 * Marks this Edge as being online. This is called by an event listener.
	 */
	public synchronized void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
		this.onSetOnline.forEach(listener -> listener.accept(isOnline));
	}

	/*
	 * Config
	 */
	private final List<Consumer<JsonObject>> onSetConfig = new CopyOnWriteArrayList<>();

	public void onSetConfig(Consumer<JsonObject> listener) {
		this.onSetConfig.add(listener);
	}

	public synchronized void setConfig(JsonObject jConfig) {
		if (this.jConfig == null || !jConfig.equals(this.jConfig)) { // on change
			this.jConfig = jConfig;
			this.onSetConfig.forEach(listener -> listener.accept(jConfig));
		}
	}

	/*
	 * State
	 */
	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	/*
	 * Last Message
	 */
	private final List<Runnable> onSetLastMessage = new CopyOnWriteArrayList<>();

	public void onSetLastMessage(Runnable listener) {
		this.onSetLastMessage.add(listener);
	}

	public synchronized void setLastMessage() {
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		this.lastMessage = now;
		this.onSetLastMessage.forEach(listener -> listener.run());
	}

	public ZonedDateTime getLastMessage() {
		return lastMessage;
	}

	/*
	 * Last Update
	 */
	private final List<Runnable> onSetLastUpdate = new CopyOnWriteArrayList<>();

	public void onSetLastUpdate(Runnable listener) {
		this.onSetLastUpdate.add(listener);
	}

	public synchronized void setLastUpdate() {
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		this.lastUpdate = now;
		this.onSetLastUpdate.forEach(listener -> listener.run());
	}

	public ZonedDateTime getLastUpdate() {
		return lastUpdate;
	}

	/*
	 * Version
	 */
	private final List<Consumer<String>> onSetVersion = new CopyOnWriteArrayList<>();

	public void onSetVersion(Consumer<String> listener) {
		this.onSetVersion.add(listener);
	}

	public synchronized void setVersion(String version) {
		if (this.version == null || !version.equals(this.version)) { // on change
			log.info("Edge [" + this.getId() + "]: Update version to [" + version + "]. It was [" + this.version + "]");
			this.version = version;
			this.onSetVersion.forEach(listener -> listener.accept(version));
		}
	}

	/*
	 * State of Charge (SoC)
	 */
	private final List<Consumer<Integer>> onSetSoc = new CopyOnWriteArrayList<>();

	public void onSetSoc(Consumer<Integer> listener) {
		this.onSetSoc.add(listener);
	}

	public synchronized void setSoc(int soc) {
		if (this.soc == null || this.soc.intValue() != soc) { // on change
			log.info("Edge [" + this.getId() + "]: Update SoC to [" + soc + "]. It was [" + this.soc + "]");
			this.soc = soc;
			this.onSetSoc.forEach(listener -> listener.accept(soc));
		}
	}

	/*
	 * IPv4
	 */
	private final List<Consumer<String>> onSetIpv4 = new CopyOnWriteArrayList<>();

	public void onSetIpv4(Consumer<String> listener) {
		this.onSetIpv4.add(listener);
	}

	public synchronized void setIpv4(String ipv4) {
		if (this.ipv4 == null || !ipv4.equals(this.ipv4)) { // on change
			log.info("Edge [" + this.getId() + "]: Update IPv4 to [" + ipv4 + "]. It was [" + this.ipv4 + "]");
			this.ipv4 = ipv4;
			this.onSetIpv4.forEach(listener -> listener.accept(ipv4));
		}
	}
}
