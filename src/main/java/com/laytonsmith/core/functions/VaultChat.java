

package com.laytonsmith.core.functions;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;

/**
 *
 * @author RyanTheLeach
 * Based off of Economy.java
 */
public class VaultChat {
    	
    private static void CheckInstallation() throws ConfigRuntimeException{
        boolean failure = true;
        try{
            chat.getName();
            failure = false;
        } catch(NoClassDefFoundError e){            
        } catch(NullPointerException e){            
        }
        if(failure){
            throw new ConfigRuntimeException("You are attempting to use"
                    + " a Vault chat function, and your Vault chat setup is not valid."
                    + " Please install Vault and a Chat metadata plugin before attempting"
                    + " to use any of the Economy functions.", ExceptionType.InvalidPluginException, Target.UNKNOWN);
        }
    }
    
  //Small abstraction layer around the economy plugin handler
    private static class ChatWrapper {
        
        String name;

        private ChatWrapper(String name) { 
            CheckInstallation();
            this.name = name;
        }
      	  	
        private String getPrefix(String world){
        	return chat.getPlayerPrefix(name, world);
        }
                
    }
        
    private static net.milkbowl.vault.chat.Chat chat;
    
    public static Boolean setupChat(){
        net.milkbowl.vault.chat.Chat chatProvider = Static.getServer().getVaultChat();
        if (chatProvider != null) {
            chat = chatProvider;
        }
        return (chat != null);
    }
    
    public static String docs(){
        return "Provides functions to hook into the server's chat metadata plugin. To use any of these functions, you must have one of the"
                + " following economy plugins installed: DroxPerms, GroupManager, Permissions 3, PermissionsEx, Privleges, TotalPermissions,"
                + " bPermissions, bPermissions2, iChat, mChat, mChatSuite, rscPermissions, zPermissions or any other vault compatible provider"
                + " In addition, you must download the [http://dev.bukkit.org/server-mods/vault/ Vault plugin]. Beyond this,"
                + " there is no special setup to get the chat variable functions working, assuming they work for you in game using"
                + " the plugin's default controls.";
    }
    
    @api public static class chat_prefix extends AbstractFunction{

        public String getName() {
            return "chat_prefix";
        }

        public Integer[] numArgs() {
            return new Integer[]{0,1};
        }

        public String docs() {
            return "string {player} Returns the prefix of the given player.";
        }

        public ExceptionType[] thrown() {
            return new ExceptionType[]{ExceptionType.PlayerOfflineException,ExceptionType.PluginInternalException, ExceptionType.InvalidPluginException};
        }

        public boolean isRestricted() {
            return false;
        }
        public CHVersion since() {
            return CHVersion.V3_3_1;
        }

        public Boolean runAsync() {
            return null;
        }

        public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
        	MCPlayer p = GetPlayer(t,env,args);
          String prefix = new ChatWrapper(p.getName()).getPrefix(p.getWorld().getName());
          if (prefix == null) return new CNull(t);
          else return new CString(prefix, t);
        }
        
    }
    
    
    private static MCPlayer GetPlayer(Target tile, Environment env, Construct ... args){
    	MCCommandSender m = env.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			MCPlayer player = null;
			if (args.length == 0) {
				player = (m instanceof MCPlayer ? ((MCPlayer) m) : null);
			} else if (args.length == 1) {
				player = Static.GetPlayer(args[0].val(),tile);
			}
      Static.AssertPlayerNonNull(player, tile);
      return player;
    }
}
