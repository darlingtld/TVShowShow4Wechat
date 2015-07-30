package lingda.tang.engine;

import lingda.tang.pojo.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by darlingtld on 2015/2/8.
 */
@Component
public class MovieEngine {

    @Autowired
    private BttiantangProvider bttiantangProvider;
    @Autowired
    private DygodProvider dygodProvider;

    private final int ALIVE_TIME = 60;
    private final int RESULT_WAIT_TIME = 60;

    private List<SourceProvider> providerList = new ArrayList<>();

    @PostConstruct
    public void init() {
        providerList.add(bttiantangProvider);
        providerList.add(dygodProvider);
    }

    public List<Show> searchShows(final String keyword) {
        final List<Show> showList = new ArrayList<>();
        List<Future> futureList = new ArrayList<Future>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1, true));
        for (final SourceProvider provider : providerList) {
            futureList.add(threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    showList.addAll(provider.searchShows(keyword));
                }
            }));
        }
        for (Future future : futureList) {
            try {
                future.get(RESULT_WAIT_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        return showList;
    }
}
