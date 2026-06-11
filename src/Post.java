import java.util.ArrayList;

public class Post {
    private String authorName;
    private String type;
    private int emotionalWeight;
    private ArrayList<Comment> comments;

    public Post(String authorName, String type, int emotionalWeight) {
        this.authorName = authorName;
        this.type = type;
        this.emotionalWeight = emotionalWeight;
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public String getAuthorName() { return authorName; }
    public String getType() { return type; }
    public int getEmotionalWeight() { return emotionalWeight; }
    public ArrayList<Comment> getComments() { return comments; }
}