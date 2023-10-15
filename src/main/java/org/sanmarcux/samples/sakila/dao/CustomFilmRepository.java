package org.sanmarcux.samples.sakila.dao;

import org.sanmarcux.samples.sakila.dto.FilmActorDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class CustomFilmRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomFilmRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FilmActorDTO> getFilmWithActors(short filmId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("get_film_with_actors")
                .returningResultSet("p_cursor", new RowMapper<FilmActorDTO>() {
                    @Override
                    public FilmActorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        FilmActorDTO dto = new FilmActorDTO();
                        dto.setFilmId(rs.getLong("film_id"));
                        dto.setFilmTitle(rs.getString("film_title"));
                        dto.setFilmDescription(rs.getString("film_description"));
                        dto.setActorId(rs.getLong("actor_id"));
                        dto.setActorFirstName(rs.getString("actor_first_name"));
                        dto.setActorLastName(rs.getString("actor_last_name"));
                        return dto;
                    }
                });

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_film_id", filmId);

        Map<String, Object> result = jdbcCall.execute(in);
        return (List<FilmActorDTO>) result.get("p_cursor");
    }
}
