package com.freightfox.githubsearcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Persisted representation of a GitHub repository.
 *
 * <p>The natural/business key is {@link #githubId} (the id assigned by GitHub), which is
 * declared unique so that repeated searches for the same repository result in an upsert
 * (update-if-exists, insert-otherwise) rather than duplicate rows.
 */
@Entity
@Table(name = "repositories", uniqueConstraints = @UniqueConstraint(columnNames = "github_id"))
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "programming_language")
    private String language;

    @Column(name = "stars_count")
    private Long stars;

    @Column(name = "forks_count")
    private Long forks;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    public Repository() {
    }

    public Repository(Long githubId, String name, String description, String ownerName,
                      String language, Long stars, Long forks, LocalDateTime lastUpdated,
                      LocalDateTime fetchedAt) {
        this.githubId = githubId;
        this.name = name;
        this.description = description;
        this.ownerName = ownerName;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
        this.lastUpdated = lastUpdated;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGithubId() {
        return githubId;
    }

    public void setGithubId(Long githubId) {
        this.githubId = githubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getStars() {
        return stars;
    }

    public void setStars(Long stars) {
        this.stars = stars;
    }

    public Long getForks() {
        return forks;
    }

    public void setForks(Long forks) {
        this.forks = forks;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    /**
     * Applies values from another {@link Repository} onto this instance, keeping the
     * primary key intact. Used by the upsert logic when a repository already exists.
     */
    public void updateFrom(Repository source) {
        this.name = source.name;
        this.description = source.description;
        this.ownerName = source.ownerName;
        this.language = source.language;
        this.stars = source.stars;
        this.forks = source.forks;
        this.lastUpdated = source.lastUpdated;
        this.fetchedAt = source.fetchedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Repository))
            return false;
        Repository that = (Repository) o;
        return Objects.equals(githubId, that.githubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubId);
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", githubId=" + githubId +
                ", name='" + name + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", language='" + language + '\'' +
                ", stars=" + stars +
                ", forks=" + forks +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
