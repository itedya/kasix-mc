package com.itedya.skymaster.runnables.island;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandRateDao;
import com.itedya.skymaster.dtos.database.IslandRateDto;
import com.itedya.skymaster.enums.IslandRate;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class RatePlayerIslandRunnable extends SkymasterRunnable {
    private Player player;
    private int islandId;
    private int rateValue;

    public RatePlayerIslandRunnable(Player player, int islandId, int rateValue) {
        super(player,true);
        this.player = player;
        this.islandId = islandId;
        this.rateValue = rateValue;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
            // if user already voted up
            IslandRateDao rateSchematicDao = new IslandRateDao(connection);
            var schematicDto = rateSchematicDao.getByIslandId_ratingPlayerUUID(islandId,player.getUniqueId().toString(),false);

            //player voted
            if(schematicDto!=null ){
                //already voted with given rating
                if(schematicDto.value == rateValue){
                    player.sendMessage(ChatColor.RED + "Juz zaglosowales w taki sposob");
                }
                //voted but with different value/"changed" rating value
                else{
                    rateSchematicDao.create(islandId, player.getUniqueId().toString(), rateValue);
                    connection.commit();
                    player.sendMessage(ChatColor.RED + "Zaglosowales pomyslnie: "+rateValue);
                }
            }
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
