package com.cherchy.markod.repository;

import com.cherchy.markod.model.Market;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends MongoRepository<Market, String> {

    GeoResults<Market> findByLocationNear(Point location, Distance distance);

    List<Market> findByLocationWithin(Polygon polygon);
}
