import java.util.ArrayList;
import java.util.Random;

public class NetworkSystem {
    private ArrayList<User> users;
    private ArrayList<Post> posts;
    private Random random;

    public NetworkSystem() {
        this.users = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.random = new Random();
    }

    public void init() {
        users.clear();
        posts.clear();

        users.add(new Extravert("Alex", 5));
        users.add(new Extravert("Boris", 5));

        users.add(new Introvert("Elena", 30));
        users.add(new Introvert("Dmitry", 25));
        users.add(new Introvert("Anna", 35));

        users.add(new Troll("Vlad_Troll", 10));
        users.add(new Troll("Super_Toxic", 10));

        users.add(new User("Max_Podpevala", "Подпевала", 15));
        users.add(new User("Ivan_Podpevala", "Подпевала", 15));
        users.add(new User("Svetlana", "Обычный пользователь", 15));

        User troll1 = findUser("Vlad_Troll");
        User troll2 = findUser("Super_Toxic");
        User podpevala1 = findUser("Max_Podpevala");
        User podpevala2 = findUser("Ivan_Podpevala");

        if (troll1 != null && podpevala1 != null) troll1.addFriend(podpevala1);
        if (troll1 != null && podpevala2 != null) troll1.addFriend(podpevala2);
        if (troll2 != null && podpevala1 != null) troll2.addFriend(podpevala1);

        User ext1 = findUser("Alex");
        User intr1 = findUser("Elena");
        User intr2 = findUser("Dmitry");
        if (ext1 != null && intr1 != null) ext1.addFriend(intr1);
        if (ext1 != null && intr2 != null) ext1.addFriend(intr2);
    }

    public User findUser(String name) {
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u;
            }
        }
        return null;
    }

    public void callSycophants(Troll troll, Post post) {
        for (User friend : troll.getFriends()) {
            if (friend.getType().equals("Подпевала") && friend.isOnline()) {
                Comment badComment = new Comment(friend.getName(), friend.getType(), "Поддакивающий комментарий", -5);
                post.addComment(badComment);

                User victim = findUser(post.getAuthorName());
                if (victim != null) {
                    victim.changeMood(-5);
                }
            }
        }
    }

    public String update() {
        ArrayList<User> onlineUsers = new ArrayList<>();
        for (User u : users) {
            if (u.isOnline()) onlineUsers.add(u);
        }

        if (onlineUsers.isEmpty()) {
            return "Все пользователи покинули сеть. Моделирование завершено.";
        }

        User activeUser = onlineUsers.get(random.nextInt(onlineUsers.size()));
        double actionChance = random.nextDouble();

        if (actionChance < 0.30) {
            String[] postTypes = {"Веселый", "Грустный", "Нейтральный"};
            String chosenType = postTypes[random.nextInt(postTypes.length)];
            int weight = random.nextInt(20) + 10;

            Post newPost = activeUser.createPost(chosenType, weight);
            this.posts.add(newPost);

            return "ПОСТ: " + activeUser.getName() + " (" + activeUser.getType() + ") опубликовал " + chosenType + " пост.";
        }
        else if (!posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            return activeUser.reactToPost(lastPost, this);
        }

        return "В сети временное затишье...";
    }

    public ArrayList<User> getUsers() { return users; }
    public ArrayList<Post> getPosts() { return posts; }

    public void reset() {
        init();
    }
}
