public class Comment {
    private String authorName;
    private String authorType;
    private String text;
    private int toxicity;

    public Comment(String authorName, String authorType, String text, int toxicity) {
        this.authorName = authorName;
        this.authorType = authorType;
        this.text = text;
        this.toxicity = toxicity;
    }

    public String getAuthorName() { return authorName; }
    public String getAuthorType() { return authorType; }
    public String getText() { return text; }
    public int getToxicity() { return toxicity; }
}
