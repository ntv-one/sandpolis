module com.sandpolis.plugin.sysinfo.server.vanilla {
	exports com.sandpolis.plugin.sysinfo.server.vanilla;

	requires com.sandpolis.core.instance;
	requires com.sandpolis.core.net;
	
	provides com.sandpolis.core.instance.plugin.SandpolisPlugin with com.sandpolis.plugin.sysinfo.server.vanilla.SysinfoPlugin;
}
