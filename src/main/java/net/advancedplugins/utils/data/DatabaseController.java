package net.advancedplugins.utils.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.LruObjectCache;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.LogBackendType;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.advancedplugins.utils.ReflectionUtil;
import net.advancedplugins.utils.data.connection.ConnectionType;
import net.advancedplugins.utils.data.connection.IConnectionHandler;
import net.advancedplugins.utils.data.connection.MySQLConnectionHandler;
import net.advancedplugins.utils.data.connection.SQLiteConnectionHandler;
import net.advancedplugins.utils.data.persister.base.*;
import net.advancedplugins.utils.trycatch.TryCatchUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter @Setter
public class DatabaseController {
    private final JavaPlugin plugin;
    private final File jarFile;
    private final ConnectionType connectionType;
    private final ConfigurationSection options;

    private boolean debug;
    private JdbcPooledConnectionSource source;
    private Map<Class<?>, Dao<?,?>> daoMap;

    /**
     * Initialize database controller which is basing on ORMLite
     * Requires com.j256.ormlite:ormlite-jdbc:6.1 in plugin.yml (libraries section)
     * @param plugin JavaPlugin instance
     * @param jarFile JavaPlugin#getFile (Unfortunately it's protected, so it needs to be got from the main class)
     * @param connectionType Type of connection
     * @param options Config's configuration section with connection options: host,port,username,password,database,cache (values for cache: -1 [disabled], 0 [cache with garbage collection], other value [cache with limited storage])
     */
    public DatabaseController(JavaPlugin plugin, File jarFile, ConnectionType connectionType, ConfigurationSection options) {
        this.plugin = plugin;
        this.jarFile = jarFile;
        this.connectionType = connectionType;
        this.options = options;

        this.debug = false;
        this.daoMap = new HashMap<>();
    }

    /**
     * Connect to the database
     */
    public void connect() {
        IConnectionHandler handler = null;
        switch (this.connectionType) {
            case MYSQL:
                handler = new MySQLConnectionHandler();
                break;
            case SQLITE:
                handler = new SQLiteConnectionHandler(this.plugin.getDataFolder());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported connection type: " + this.connectionType.name());
        }
        handler.retrieveCredentials(this.options);
        this.source = TryCatchUtil.tryAndReturn(handler::connect);
        this.registerDefaultPersisters();

        if(!this.debug) LoggerFactory.setLogBackendFactory(LogBackendType.NULL);
    }

    /**
     * Close database connection
     */
    @SneakyThrows
    public void close() {
        if(this.source == null) return;
        this.source.close();
    }

    /**
     * Register all entities in certain package
     * @param packageName Name of package
     */
    public void registerEntities(String packageName) {
        if(this.source == null) return;

        for(Class<?> clazz :ReflectionUtil.getAllClassesInPackage(this.jarFile, packageName)) {
            registerEntity(clazz);
        }
    }

    /**
     * Register entity
     * @param clazz Entity class
     */
    public void registerEntity(Class<?> clazz) {
        if(this.source == null) return;

        if(clazz.getDeclaredAnnotation(DatabaseTable.class) == null) return;

        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> {
                    DatabaseField ann = f.getDeclaredAnnotation(DatabaseField.class);
                    if(ann == null) return false;
                    return ann.id() || ann.generatedId() || !ann.generatedIdSequence().isEmpty();
                })
                .findAny().orElse(null);

        if(idField == null) return;

        TryCatchUtil.tryRun(() -> TableUtils.createTableIfNotExists(this.source, clazz));
        Dao<?,?> dao = TryCatchUtil.tryAndReturn(() -> DaoManager.createDao(this.source,clazz));

        int cache = this.options.getInt("cache",0);
        switch (cache) {
            case -1:
                break;
            case 0:
                TryCatchUtil.tryRun(() -> dao.setObjectCache(true));
                break;
            default:
                TryCatchUtil.tryRun(() -> dao.setObjectCache(new LruObjectCache(cache)));
                break;
        }
        this.daoMap.put(clazz, dao);
    }

    @SuppressWarnings("unchecked")
    public <T,Z> Dao<T,Z> getDao(Class<T> daoClass, Class<Z> idClass) {
        if(this.source == null) return null;

        Dao<?,?> dao = this.daoMap.get(daoClass);
        return dao == null ? null : (Dao<T,Z>) dao;
    }

    /**
     * Register all OrmLite persisters from specified package
     * @param packageName Package where are stored all persisters
     */
    public void registerPersisters(String packageName) {
        if(this.source == null) return;
        ReflectionUtil.getAllClassesInPackage(this.jarFile, packageName)
                .stream()
                .filter(DataPersister.class::isAssignableFrom)
                .map(clazz -> TryCatchUtil.tryAndReturn(() -> (DataPersister) clazz.getDeclaredConstructor().newInstance()))
                .filter(Objects::nonNull)
                .forEach(DataPersisterManager::registerDataPersisters);
    }

    /**
     * Register persisters
     * @param persisters Persisters instance
     */
    public void registerPersisters(DataPersister... persisters) {
        DataPersisterManager.registerDataPersisters(persisters);
    }

    private void registerDefaultPersisters() {
        registerPersisters(
                new ItemStackPersister(),
                new ListPersister(),
                new LocationPersister(),
                new MapPersister(),
                new OfflinePlayerPersister(),
                new WorldPersister()
        );
    }
}