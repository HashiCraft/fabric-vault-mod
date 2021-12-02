# Vault demo

```shell
cd /mnt/c/minecraft/fabric-vault-mod/vault
```

```shell
export VAULT_ADDR=http://localhost:8200
```

Set up Vault to enable transit engine and approle authentication.
```shell
./setup.sh
```

Start the Vault agent with the given config.
```shell
./agent.sh
```





## Step 1 - Encrypt the data.
```java
// block/Dispenser.java @ onUse
String data = dispenser.encrypt(player.getUuid().toString());
if (data == null) {
  return ActionResult.SUCCESS;
}
```

```java
// block/DispenserEntity.java @ encrypt
try {
  // Construct the payload and encode the data for transport.
  String payload = String.format("""
      {
      "plaintext": "%s"
      }
      """, Base64.getEncoder().encodeToString(input.getBytes()));

  // Convert it to JSON.
  StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

  // Create the HTTP request.
  HttpClient httpClient = HttpClientBuilder.create().build();
  HttpPost request = new HttpPost("http://localhost:8100/v1/transit/encrypt/minecraft");
  request.setEntity(entity);

  // Execute the HTTP request.
  HttpResponse response = httpClient.execute(request);
  String body = EntityUtils.toString(response.getEntity());

  // Check if everything went well.
  if (response.getStatusLine().getStatusCode() != 200) {
    System.out.println(body);
    return null;
  }

  // Get the ciphertext from the response.
  JsonObject data = new JsonParser().parse(body).getAsJsonObject();
  return data.get("data").getAsJsonObject().get("ciphertext").getAsString();
} catch (IOException e) {
  System.out.println("ERROR: " + e.getMessage());
  return null;
}
```






## Step 2 - Sign the data.
```java
// block/Dispenser.java @ onUse
String signature = dispenser.sign(data);
if (signature == null) {
  return ActionResult.SUCCESS;
}
```

```java
// block/DispenserEntity.java @ sign
try {
  // Construct the payload.
  String payload = String.format("""
      {
        "hash_algorithm":"sha2-256",
        "signature_algorithm":"pkcs1v15",
        "input":"%s"
      }
      """, Base64.getEncoder().encodeToString(input.getBytes()));

  // Convert it to JSON.
  StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

  // Create the HTTP request.
  HttpClient httpClient = HttpClientBuilder.create().build();
  HttpPost request = new HttpPost("http://localhost:8100/v1/transit/sign/minecraft");
  request.setEntity(entity);

  // Execute the HTTP request.
  HttpResponse response = httpClient.execute(request);
  String body = EntityUtils.toString(response.getEntity());

  // Check if everything went well.
  if (response.getStatusLine().getStatusCode() != 200) {
    System.out.println(body);
    return null;
  }

  // Get the signature from the response.
  JsonObject data = new JsonParser().parse(body).getAsJsonObject();
  return data.get("data").getAsJsonObject().get("signature").getAsString();
} catch (IOException e) {
  System.out.println("ERROR: " + e.getMessage());
  return null;
}
```






## Step 3 - Write the data to the keycard.
```java
// block/Dispenser.java @ onUse
NbtCompound identity = itemStack.getOrCreateNbt();
identity.putString("name", player.getName().asString());
identity.putString("data", data);
identity.putString("signature", signature);
itemStack.setNbt(identity);
```





## Step 4 - Get the identity from the keycard.
```java
// Lock.java @ onUse
// Read the identity from the card.
NbtCompound identity = stack.getNbt();
if (identity == null) {
  player.sendMessage(new LiteralText("ACCESS DENIED - Keycard not valid"), true);
  return ActionResult.SUCCESS;
}

// Read the data from the identity.
String name = identity.getString("name");
String data = identity.getString("data");
String signature = identity.getString("signature");
if (signature == null) {
  player.sendMessage(new LiteralText("ACCESS DENIED - Could not read signature"), true);
  return ActionResult.SUCCESS;
}
```






## Step 5 - Verify the signature.
```java
// Lock.java @ onUse
boolean valid = lock.verify(data, signature);
```

```java
// LockEntity.java - verify
try {
  // Construct the payload and encode the data for transport.
  String payload = String.format("""
      {
        "hash_algorithm":"sha2-256",
        "signature_algorithm":"pkcs1v15",
        "input":"%s",
        "signature": "%s"
      }
      """, Base64.getEncoder().encodeToString(input.getBytes()), signature);

  // Convert it to JSON.
  StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

  // Create the HTTP request.
  HttpClient httpClient = HttpClientBuilder.create().build();
  HttpPost request = new HttpPost("http://localhost:8100/v1/transit/verify/minecraft");
  request.setEntity(entity);

  // Execute the HTTP request.
  HttpResponse response = httpClient.execute(request);
  String body = EntityUtils.toString(response.getEntity());

  // Check if everything went well.
  if (response.getStatusLine().getStatusCode() != 200) {
    System.out.println(body);
    return false;
  }

  // Get the validity from the response.
  JsonObject data = new JsonParser().parse(body).getAsJsonObject();
  return data.get("data").getAsJsonObject().get("valid").getAsBoolean();
} catch (IOException e) {
  System.out.println("ERROR: " + e.getMessage());
  return false;
}
```






## Step 6 - Decrypt the data.
```java
// Lock.java @ onUse
if (valid) {
  // Decrypt the data.
  String decrypted = lock.decrypt(data);
  if (decrypted == null) {
    player.sendMessage(new LiteralText("ACCESS DENIED - Could not decrypt data"), true);
    return ActionResult.SUCCESS;
  }

  // Decode the base64 encoded data.
  String uuid = new String(Base64.getDecoder().decode(decrypted));

  // Emit a redstone signal.
  world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_NEIGHBORS);
  world.getBlockTickScheduler().schedule(pos, this, 40);

  player.sendMessage(new LiteralText(String.format("Welcome %s (%s)", name, uuid)), true);
} else {
  player.sendMessage(new LiteralText("ACCESS DENIED - Keycard not valid"), true);
  return ActionResult.SUCCESS;
}
```

```java
// LockEntity.java @ onUse
try {
  // Construct the payload.
  String payload = String.format("""
      {
      "ciphertext": "%s"
      }
      """, input);

  // Convert it to JSON.
  StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

  // Create the HTTP request.
  HttpClient httpClient = HttpClientBuilder.create().build();
  HttpPost request = new HttpPost("http://localhost:8100/v1/transit/decrypt/minecraft");
  request.setEntity(entity);

  // Execute the HTTP request.
  HttpResponse response = httpClient.execute(request);
  String body = EntityUtils.toString(response.getEntity());

  // Check if everything went well.
  if (response.getStatusLine().getStatusCode() != 200) {
    System.out.println(body);
    return null;
  }

  // Get the ciphertext from the response.
  JsonObject data = new JsonParser().parse(body).getAsJsonObject();
  return data.get("data").getAsJsonObject().get("plaintext").getAsString();
} catch (IOException e) {
  System.out.println("ERROR: " + e.getMessage());
  return null;
}
```