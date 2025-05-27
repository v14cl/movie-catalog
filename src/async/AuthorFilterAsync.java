package async;

import models.*;
import memoization.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class AuthorFilterAsync {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AuthorMoviesCountMemoizer memoizer;

    public AuthorFilterAsync(AuthorMoviesCountMemoizer memoizer) {
        this.memoizer = memoizer;
    }

    public CompletableFuture<List<Author>> filterAuthorsAsync(
            List<Author> authors,
            Predicate<Integer> countCondition,
            AtomicBoolean isCancelled) {

        List<CompletableFuture<Author>> futures = new ArrayList<>();

        for (Author author : authors) {
            CompletableFuture<Author> future = CompletableFuture.supplyAsync(() -> {
                if (isCancelled.get()) {
                    throw new CancellationException("Operation was canceled");
                }

                try {
                    int count = memoizer.getCount(author.getId());
                    if (countCondition.test(count)) {
                        return author;
                    }
                } catch (Exception e) {
                    System.err.println(
                            "Error while counting author films" + author.getName() + ": " + e.getMessage());
                }
                return null;
            }, executor);

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<Author> result = new ArrayList<>();
                    for (CompletableFuture<Author> future : futures) {
                        try {
                            Author a = future.get();
                            if (a != null)
                                result.add(a);
                        } catch (Exception e) {
                        }
                    }
                    return result;
                });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
