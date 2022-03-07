package com.jmhreif.azulneo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class AzulNeo4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzulNeo4jApplication.class, args);
	}

}

@RestController
@RequestMapping("/versions")
class JavaVersionController {
	private final JavaVersionRepository repo;

	public JavaVersionController(JavaVersionRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/read")
	Iterable<JavaVersion> findAllJavaVersion() { return repo.findAll(); }

	@GetMapping("/read/{javaVersion}")
	Optional<JavaVersion> findJavaVersion(@PathVariable String javaVersion) { return repo.findById(javaVersion); }

	@PostMapping("/create")
	JavaVersion createJavaVersion(@RequestBody JavaVersion release) { return repo.save(release); }

	@PutMapping("/update/{javaVersion}")
	JavaVersion updateJavaVersion(@RequestBody JavaVersion newVersion, @PathVariable String javaVersion) {
		return repo.findById(javaVersion)
				.map(release -> {
					release.setStatus(newVersion.getStatus());
					release.setGaDate(newVersion.getGaDate());
					release.setEolDate(newVersion.getEolDate());
					return repo.save(release);
				})
				.orElseGet(() -> repo.save(newVersion));
	}

	@DeleteMapping("/delete/{javaVersion}")
	void deleteJavaVersion(@PathVariable String javaVersion) { repo.deleteById(javaVersion); }
}

interface JavaVersionRepository extends Neo4jRepository<JavaVersion, String> {

}

@Node
class JavaVersion {
	@Id
	@Property("version")
	private String javaVersion;
	private String status;
	private LocalDate gaDate, eolDate;

	@Relationship("FROM_NEWER")
	private List<VersionDiff> olderVersionDiffs;

	public JavaVersion(String javaVersion, String status, LocalDate gaDate, LocalDate eolDate) {
		this.javaVersion = javaVersion;
		this.status = status;
		this.gaDate = gaDate;
		this.eolDate = eolDate;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getGaDate() {
		return gaDate;
	}

	public void setGaDate(LocalDate gaDate) {
		this.gaDate = gaDate;
	}

	public LocalDate getEolDate() {
		return eolDate;
	}

	public void setEolDate(LocalDate eolDate) {
		this.eolDate = eolDate;
	}

	public List<VersionDiff> getOlderVersionDiffs() {
		return olderVersionDiffs;
	}
}

@Node
class VersionDiff {
	@Id
	@GeneratedValue
	private Long neoId;
	private String fromVendor, fromVersion, toVendor, toVersion;

	public VersionDiff(Long neoId, String fromVendor, String fromVersion, String toVendor, String toVersion) {
		this.neoId = neoId;
		this.fromVendor = fromVendor;
		this.fromVersion = fromVersion;
		this.toVendor = toVendor;
		this.toVersion = toVersion;
	}

	public Long getNeoId() {
		return neoId;
	}

	public void setNeoId(Long neoId) {
		this.neoId = neoId;
	}

	public String getFromVendor() {
		return fromVendor;
	}

	public void setFromVendor(String fromVendor) {
		this.fromVendor = fromVendor;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getToVendor() {
		return toVendor;
	}

	public void setToVendor(String toVendor) {
		this.toVendor = toVendor;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}
}
