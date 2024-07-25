//! # Agent Instance
//!
//! At a general level, agents are responsible for carrying out tasks for remote
//! users. They can connect over the network to any other type of instance. At
//! minimum, agents must be associated with one server.
//!
//! ## Instance Configuration
//!
//! ```py
//! # com.sandpolis.agent
//! {
//!   "network" : {
//!     "servers"          : [
//!       String(), # A server hostname and port
//!     ],
//!     "timeout"          : Number(default=1000),  # The server connection timeout in milliseconds
//!     "cooldown"         : Number(default=1000),  # How long to wait after each connection
//!     "strict_certs"     : Boolean(default=True), # The agent will refuse to connect to a server that presents an invalid certificate
//!     "polling_interval" : Number(default=0),     # The connection poll interval in seconds
//!   },
//!   "auth" : {
//!     "password"    : String(),
//!     "certificate" : String(),
//!   },
//!   "collectors": [
//!   	String()
//!   ]
//! }
//! ```
//!
//! ## Connection Modes
//!
//! There are two connection modes that have an impact on performance and latency.
//!
//! ### Continuous
//!
//! In continuous mode, the agent maintains its primary connection at all times. If
//! the connection is lost, the agent will periodically attempt to reestablish the
//! connection using the same parameters it used to establish the initial
//! connection.
//!
//! The connection mode can be changed on-the-fly by a user or scheduled to change
//! automatically according to the time and day.
//!
//! ### Polling
//!
//! In polling mode, the agent intentionally closes the primary connection unless
//! there exists an active stream. On a configurable schedule, the agent reconnects
//! to a server, flushes any cached data, and checks for any new work items. After
//! executing all available work items, the primary connection is closed again.
//!
//! The agent may attempt a spontaneous connection outside of the regular schedule
//! if an internal agent process triggers it.
//!
//! ## Plugins
//!
//! Agents can optionally support plugins to enhance functionality beyond the
//! standard feature set. Upon initial connection, the agent provides a list of
//! plugin versions that it has loaded. The server responds with a list of plugin
//! archives that the agent should install.
//!
//! ## Standard Feature Set
//!
//! The standard feature set is the minimum amount of functionality an agent
//! implementation must provide.
//!
//! ### AgentMetadata
//!
//! ## Upgrades
//!
//! There are two ways to upgrade the agent:
//!
//! - automatically by sending the update command to the server,
//! - manually by generating a new installer and executing it on the agent
//!
//! ### Manual Upgrade
//!
//! A manual upgrade is triggered when an installer is executed on the agent and the
//! relevant base directory is already populated with an installation. If the agent
//! is not running, the installer will overwrite the base directory and install
//! itself. Any data that the agent has cached but not sent to the server will be
//! lost!
//!
//! Advantages
//!
//! - This is the only way to upgrade if the agent can no longer connect to the
//!   server
//!
//! Disadvantages
//!
//! - Manual intervention required
//! - Cached data may be lost
//!
//! ### Automated Upgrade
//!
//! If the agent is connected to a server, it can be upgraded remotely. This will
//! cause the server to fetch the agent configuration, generate a new installer, and
//! transfer it to the agent. The agent then executes the new installer and
//! terminates.
//!
//! ### Container Resident
//!
//! ## Boot Agent
//!
//! Several boot agent operations are accessible from typical agents.
//!
//! ### Installation
//!
//! Typical agent instances are capable of installing a boot agent on their host
//! machine if installed with sufficient permissions. Write access to the `EFI`
//! directory on the ESP partition is required for installation and uninstallation.
//!
//! The boot agent artifact is a standard `.efi` file generated by a server
//! instance. The agent simply copies the file to the ESP partition.
//!
//! ### Uninstallation
//!
//! To uninstall, an agent must simply delete the boot agent from the ESP partition.
//!
//! ### Reboot into boot agent
//!
//! Agents can launch the boot agent indirectly by setting the `BootNext` variable
//! to the index of the boot agent's EFI entry and rebooting the machine.
//!
//!
//! ## Installation Types
//!
//! There are three ways to install the agent, each with advantages and
//! disadvantages.
//!
//! ### Package Manager Installation
//!
//! This option makes installation itself easy, but requires some effort to
//! configure the agent to connect to a server. This installation type also means
//! that upgrades must only occur through the package manager.
//!
//! #### Filesystem Layout
//!
//! ##### Pacman (Arch Linux)
//!
//! | Path                                              | Description                |
//! | ------------------------------------------------- | -------------------------- |
//! | `/usr/lib/systemd/system/sandpolis-agent.service` | Systemd service definition |
//! | `/usr/bin/sandpolis-agent-config`                 | Configuration executable   |
//! | `/usr/bin/sandpolis-agent`                        | Main executable            |
//! | `/usr/share/java/sandpolis-agent/lib`             | `LIB` location             |
//! | `/usr/share/java/sandpolis-agent/plugin`          | `PLUGIN` location          |
//! | `/var/lib/sandpolis-agent/data`                   | `DATA` location            |
//!
//! ##### Apk (Alpine)
//!
//! ### Deployer Installation
//!
//! This option requires no explicit configuration, but the _deployer_ executable
//! must be transferred to the host and invoked explicitly.
//!
//! #### Filesystem Layout
//!
//! Deployers install everything under a single _base directory_ and create symbolic
//! links in certain locations outside of the base directory.
//!
//! | Platform | Default base directory path |
//! | -------- | --------------------------- |
//! | Linux    | `/opt/sandpolis-agent`      |
//! | Windows  |                             |
//! | macOS    |                             |
//!
//! ### Docker Installation
//!
//! This option is easy to deploy and configure, but can limit the functionality of
//! the agent in some cases.
//!
//! Certain elements of the host filesystem may be optionally mounted into the
//! container.
//!
//! ## Probe Mode
//!
//! Probes are similar to agents, but are only allowed to egress data to a server.
//! They cannot receive messages, so their configuration is immutable unless the
//! system also runs an agent capable of managing probe instances.
//!
//! This design is a security feature which ensures probe instances cannot be compromised
//! even when the gateway server is compromised.
//!
//! The only connection mode supported by probes is the _polling_ mode. On a
//! configurable schedule, the probe reconnects to a server, flushes any cached
//! data, and closes the connection.
//!
//! The probe may attempt a spontaneous connection outside of the regular schedule
//! at any time.

use anyhow::{bail, Result};
use clap::Parser;
use std::io::{IsTerminal};

use crate::core::database::Database;
use crate::CommandLine;

#[derive(Parser, Debug, Clone)]
pub struct AgentCommandLine {}

pub async fn main(args: CommandLine) -> Result<()> {
    let mut db = Database::new(None, "test", "test").await?;

    if let Some(servers) = args.server {
        for server in servers {
            db.add_server(&server, "test", "test").await?;
        }
    } else {
        if std::io::stdout().is_terminal() {
            // TODO prompt
            print!("Please enter the server's address [127.0.0.1]: ");
        } else {
            bail!("Cannot configure server");
        }
    }

    // if prompt_bool("Configure client certificate authentication?", false) {}

    // if prompt_bool("Configure password authentication? ", false) {
    //     let password = prompt_string(
    //         "Enter password: ",
    //         "",
    //         &predicate::function(|x: &String| x.len() >= 5_usize),
    //     );
    // }

    return Ok(());
}
