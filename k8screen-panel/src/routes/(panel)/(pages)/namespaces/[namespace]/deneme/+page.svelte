<script lang="ts">
  import { onMount } from 'svelte';
  import { userAPI } from '$lib/service/user-service';
  import type { UserInfo } from '$lib/model/user/UserInfo';

  let user: UserInfo;
  let socket: WebSocket;
  let namespace = 'imentis';
  let podName = 'imentis-backend-b86c48bd5-4dcxq';

  let userInput: string = '';

  // Kullanıcı profilini al
  onMount(async () => {
    user = await userAPI.getProfile();
    const wsUrl = `ws://localhost:8080/ws/exec?namespace=${namespace}&podName=${podName}&userId=${user.id}`;

    // WebSocket bağlantısını kur
    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
      console.log('WebSocket bağlantısı kuruldu');
    };

    socket.onmessage = (event) => {
      const message = event.data;
      console.log('Mesaj:', message);
    };

    socket.onerror = (error) => {
      console.error('WebSocket hatası:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket bağlantısı kapandı');
    };
  });

  const sendCommand = () => {
    if (socket && socket.readyState === WebSocket.OPEN && userInput.trim() !== '') {
      socket.send(userInput);
      userInput = '';
    }
  };
</script>

<style>
  .terminal {
    background-color: #111;
    color: #00ff00;
    font-family: monospace;
    padding: 20px;
    width: 80%;
    margin: auto;
    border: 2px solid #00ff00;
  }

  .output {
    white-space: pre-wrap;
    margin-bottom: 10px;
  }

  input {
    background-color: #111;
    color: #00ff00;
    border: none;
    width: 100%;
    font-size: 16px;
    outline: none;
    padding: 8px;
  }

  input::placeholder {
    color: #00ff00;
  }
</style>

<main>
  <div class="terminal">
    <div class="output"></div>
    <input
      type="text"
      bind:value={userInput}
      on:keydown={(e) => e.key === 'Enter' && sendCommand()}
      placeholder="Komut girin..."
    />
  </div>
</main>
