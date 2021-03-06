/*
 * Copyright 2018 Prasanth Jayachandran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.prasanthj.culvert.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.github.javafaker.Faker;

/**
 *
 */
public class Column {
  enum Type {
    BOOLEAN,
    STRING,
    STRING_DICT,
    STRING_IP_ADDRESS,
    STRING_UUID_DICT,
    LONG,
    DOUBLE,
    TIMESTAMP,
    INT_YEAR,
    INT_MONTH
  }

  private String name;
  private Type type;
  private Object[] dictionary;
  private static Faker faker;
  private static Random random;
  private static int SIZE = 1_000_000;
  private static List<String> UUIDS = new ArrayList<>();
  private static List<String> TIMESTAMPS = new ArrayList<>();
  private static List<String> IPADDRESSES = new ArrayList<>();
  private static List<Integer> YEARS = new ArrayList<>();
  private static List<Integer> MONTHS = new ArrayList<>();
  static {
    random = new Random(123);
    faker = new Faker(random);
    System.out.print("Generating " + SIZE + " fake data..");
    for (int i = 0; i < SIZE; i++) {
      UUIDS.add(UUID.randomUUID().toString());
      TIMESTAMPS.add(faker.date().birthday().toInstant().toString());
      IPADDRESSES.add(faker.internet().ipV4Address());
      YEARS.add(2000 + (faker.date().birthday().getYear() % 50));
      MONTHS.add(faker.date().birthday().getMonth() % 30); // 50 * 30 partitions max
    }
    System.out.println("Completed.");
  }

  private Column(String name, Type type, Object[] dictionary) {
    this.name = name;
    this.type = type;
    this.dictionary = dictionary;
  }

  public static class ColumnBuilder {
    private String name;
    private Type type;
    private Object[] dictionary;

    public ColumnBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public ColumnBuilder withType(Type type) {
      this.type = type;
      return this;
    }

    public ColumnBuilder withDictionary(Object[] dictionary) {
      this.dictionary = dictionary;
      return this;
    }

    public Column build() {
      return new Column(name, type, dictionary);
    }
  }

  public static ColumnBuilder newBuilder() {
    return new ColumnBuilder();
  }

  public Object getValue(final long row) {
    int rowIdx = (int) (row % SIZE);
    switch (type) {
      case BOOLEAN:
        return random.nextBoolean();
      case LONG:
        return random.nextLong();
      case DOUBLE:
        return random.nextDouble();
      case TIMESTAMP:
        return TIMESTAMPS.get(rowIdx);
      case STRING:
        return faker.name().fullName();
      case STRING_DICT:
        if (dictionary != null) {
          int randIdx = (int) (row % dictionary.length);
          return dictionary[randIdx];
        }
        // if dictionary unspecified use colors
        return faker.color().name();
      case STRING_IP_ADDRESS:
        return IPADDRESSES.get(rowIdx);
      case STRING_UUID_DICT:
        return UUIDS.get(rowIdx);
      case INT_YEAR:
        return YEARS.get(rowIdx);
      case INT_MONTH:
        return MONTHS.get(rowIdx);
      default:
        return faker.chuckNorris().fact();
    }
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public Object[] getDictionary() {
    return dictionary;
  }
}
